package helpers

object FieldHelpers {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.elements.input.f)
}
