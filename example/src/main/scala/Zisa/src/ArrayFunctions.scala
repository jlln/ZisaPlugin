package Zisa.src

/**
  * Created by james on 8/09/16.
  */
object ArrayFunctions {
  //Functions for working with 2D arrays
  def add2DIntArrays(a:Array[Array[Int]],b:Array[Array[Int]]):Array[Array[Int]] = {
    val total_length_a = a.flatten.length
    val total_length_b = b.flatten.length
    if (total_length_a != total_length_b) throw new Exception("Arrays must be the same shape (total lengths not equal")
    if (a.length != b.length) throw new Exception("Arrays must be the same shape (heights not equal)")
    a.zip(b).map{
      case (x,y) => x.zip(y).map{
        case (t,y) => t+y
      }
    }
  }

  def add3DIntArrays(a:Array[Array[Array[Int]]],b:Array[Array[Array[Int]]]):Array[Array[Array[Int]]] = {
    if (a.length != b.length) throw new Exception("Arrays must be the same shape (depths not equal)")
    a.zip(b).map{
      case (x,y) => add2DIntArrays(x,y)
    }
  }

  def addMultiple3DIntArrays(arrays:Seq[Array[Array[Array[Int]]]]):Array[Array[Array[Int]]] = {
    arrays.tail.foldLeft(arrays.head)((x,y)=>add3DIntArrays(x,y))
  }



}
