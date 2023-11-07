package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.format.DateTimeFormat

object AuthorTable : IntIdTable("author") {
    val createdAt = datetime("created_at")
    val fullName = varchar("full_name", 200)
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var createdAt by AuthorTable.createdAt
    var fullName by AuthorTable.fullName

    fun toResponse(): AuthorResponse {
        val formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val createdDateTime = createdAt.toString(formatter)

        return AuthorResponse(fullName, createdDateTime)
    }
}