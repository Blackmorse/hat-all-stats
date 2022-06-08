package common

object StringExt {
  implicit class StringExt(v: String) {
    def normalize(): String =
      v.toLowerCase
        .replace(" ", "")
        .replace("\n", "")
  }
}
