object test2 extends App with castSupport{

  val t1 = new ScriptEngineTools
  t1.eval("var String = \"this is string\"")
  t1.eval("var Int = 10")
  t1.eval("var Array = [1,2,3]")


  println(t1.getAs[String]("String")) // Some
  println(t1.getAs[Int]("Int")) // Some
  println(t1.getAs[Int]("String")) // None
  println(t1.getAs[Array[Int]]("Array")) // None
  println(t1.getAs[Array[String]]("Array")) // None
  println(t1.getAs[List[Int]]("Array")) // None

}
