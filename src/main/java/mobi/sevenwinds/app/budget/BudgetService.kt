package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val authorEntity = body.authorId?.let { AuthorEntity.findById(it) }

            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author = authorEntity
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query: Query
            if (param.fullName == null) {
                query = (BudgetTable leftJoin AuthorTable)
                    .select { BudgetTable.year eq param.year }
            } else {
                query = (BudgetTable leftJoin AuthorTable)
                    .select { BudgetTable.year eq param.year }
                    .andWhere { AuthorTable.fullName.lowerCase() eq param.fullName.lowercase() }
            }
            query.orderBy(BudgetTable.month)
                 .orderBy(BudgetTable.amount, SortOrder.DESC)

            val budgetsByYear = BudgetEntity.wrapRows(query).map { it.toResponseStats() }
            val total = budgetsByYear.count()
            val sumByType = budgetsByYear.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }
            val data = budgetsByYear.drop(param.offset).take(param.limit)

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
}