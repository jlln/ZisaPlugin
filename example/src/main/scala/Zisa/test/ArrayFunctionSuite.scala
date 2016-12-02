package Zisa.test

import org.scalatest.{FunSuite, Matchers}

/**
  * Created by james on 1/12/16.
  */
class ArrayFunctionSuite extends FunSuite with Matchers{

  val array2d_1 = Array(
    Array(1,1,1,1),
    Array(0,0,0,0)
  )
  val array2d_2 = Array(
    Array(1,2,3,4),
    Array(1,2,3,4)
  )
  test("add2DIntArrays"){
    Zisa.src.services.ArrayFunctions.add2DIntArrays(array2d_1,array2d_1) should equal(Array(Array(2,2,2,2),Array(0,0,0,0)))
    Zisa.src.services.ArrayFunctions.add2DIntArrays(array2d_1,array2d_2) should equal(Array(Array(2,3,4,5),Array(1,2,3,4)))
  }


  val array3d_1 = Array(
    Array( Array(1,2,3,4),Array(1,2,3,4)),
    Array( Array(0,0,0,0),Array(1,1,1,1)),
    Array( Array(0,0,0,0),Array(1,1,1,1))
  )
  val array3d_2 = Array(
    Array( Array(1,1,1,1),Array(1,1,1,1)),
    Array( Array(0,0,0,0),Array(1,1,1,1)),
    Array( Array(1,1,1,1),Array(1,1,1,1))
  )
  val array3d_3 = Array(
    Array( Array(2,3,4,5),Array(2,3,4,5)),
    Array( Array(0,0,0,0),Array(2,2,2,2)),
    Array( Array(1,1,1,1),Array(2,2,2,2))
  )

  test("add3DIntArrays"){
    Zisa.src.services.ArrayFunctions.add3DIntArrays(array3d_1,array3d_2) should equal(array3d_3)
  }
  test("addMultiple3DIntArrays"){
    Zisa.src.services.ArrayFunctions.addMultiple3DIntArrays(Seq(array3d_1,array3d_2,array3d_3)) should equal(Zisa.src.services.ArrayFunctions.add3DIntArrays(array3d_3,array3d_3))
  }




}
