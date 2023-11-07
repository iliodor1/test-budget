package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.annotations.type.string.length.Length
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route


fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<Unit, AuthorResponse, AuthorRequest>(info("Добавить запись")) { param, body ->
            respond(AuthorService.addRecord(body))
        }
    }
}

data class AuthorRequest(
    @Length(
        min = 3,
        max = 200,
        "Количество символов должно быть не менее 3-х и не более 200"
    ) val fullName: String
)

data class AuthorResponse(
    val fullName: String,
    val createdAt: String
)
