package sqlbuilder

object functions {
  trait func {
    def apply(field: Field): Field
  }

  object avg extends func {
    override def apply(field: Field): Field = {
      new Field(s"avg(${field.name})")
    }
  }

  object avgIf {
    def apply(field: Field, condition: String): Field = {
      new Field(s"avgIf(${field.name}, $condition)")
    }
  }

  object uniqExact {
    def apply(field: Field): Field = new Field(s"uniqExact(${field.name})")
  }

  object max extends func {
    override def apply(field: Field): Field = {
      new Field(s"max(${field.name})")
    }
  }

  object sum extends func {
    override def apply(field: Field): Field =
      new Field(s"sum(${field.name})")
  }

  object countIf {
    def apply(condition: String): Field =
      new Field(s"countIf($condition)")
  }

  object If {
    def apply(condition: String, trueEval: Field, falseEval: Field): Field = 
      new Field(s"if($condition, ${trueEval.toString()}, ${falseEval.toString()})")
  }

  object identity extends func {
    override def apply(field: Field): Field = field
  }
}
