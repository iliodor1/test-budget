package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable
                .select { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month)
                .orderBy(BudgetTable.amount, SortOrder.DESC)

            val budgetsByYear = BudgetEntity.wrapRows(query).map { it.toResponse() }
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