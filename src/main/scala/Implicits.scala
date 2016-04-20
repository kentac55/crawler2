
import Main.Data
import Implicits._
import scala.io.Source

object Implicits {

  implicit class Byte2(val src: Array[Byte]) {
    def mkString2(enc: String = detectEncode, removeCRLF: Boolean = true): String = {
      val target = if (removeCRLF) {
        src.filterNot(t => t == 13).filterNot(t => t == 10)
      } else {
        src
      }
      Source.fromBytes(target, enc).mkString("")
    }

    def detectEncode: String = {
      def test(enc: String): Boolean = {
        val srcStr = src.mkString2(enc)
        val bytes = srcStr.mkByte(enc)
        src.filterNot(t => t == 13).filterNot(t => t == 10).sameElements(bytes)
      }
      val encs = Seq(
        "EUC_JP_Solaris",
        "ms932",
        "utf8"
      )
      val result = encs.filter(enc => test(enc))
      if (result.nonEmpty) {
        result.head
      } else {
        throw new Exception("エンコード不明")
      }
    }
  }

  implicit class Str2(val src: String) {
    def mkByte(enc: String = "utf8", removeCRLF: Boolean = true): Array[Byte] = {
      if (removeCRLF) {
        src.getBytes(enc).filterNot(t => t == 13).filterNot(t => t == 10)
      } else {
        src.getBytes(enc)
      }
    }
  }

  implicit class lstData(val src: List[Data]) {
    def searchParent(target: Symbol): Option[Data] = {
      def rec(lst: List[Data], target: Symbol): Option[Data] = {
        if (lst == Nil) None
        else if (lst.head.selfSymbol == target) Some(lst.head)
        else rec(lst.tail, target)
      }
      rec(src, target)
    }

    def sortData: List[List[Data]] = {
      val exitList = src.mkExitList
      def mkMultiList(lst: List[Data] = exitList, acc: List[List[Data]] = Nil): List[List[Data]] = {
        if(lst == Nil) {
          acc
        } else{
          val current = lst.head
          val currentList = mkSingleList(List(current))
          mkMultiList(lst.tail, currentList :: acc)
        }
      }
      def mkSingleList(lst:List[Data]): List[Data] = {
        val parent = src.searchParent(lst.head.parentSymbol)
        if (parent.isDefined) {
          mkSingleList(parent.get :: lst)
        } else {
          lst
        }
      }
      mkMultiList()
    }

    def mkExitList: List[Data] = {
      def rec(lst: List[Data] = src, acc: List[Data] = Nil): List[Data] = {
        if(lst == Nil) acc
        else if(lst.head.exit) rec(lst.tail, lst.head :: acc)
        else rec(lst.tail, acc)
      }
      rec()
    }
    def extSymbol: List[(Symbol, Symbol)] = {
      def rec(lst: List[Data] = src, acc: List[(Symbol, Symbol)] = Nil): List[(Symbol, Symbol)] = {
        if (lst == Nil) acc
        else rec(lst.tail, (lst.head.parentSymbol, lst.head.selfSymbol) :: acc)
      }
      rec()
    }
  }
}

sealed trait NotNothing[A] {
  type B
}

object NotNothing {
  implicit val nothing = new NotNothing[Nothing] {
    type B = Any
  }

  implicit def notNothing[A] = new NotNothing[A] {
    type B = A
  }
}
