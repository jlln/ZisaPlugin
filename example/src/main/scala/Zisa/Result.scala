package Zisa

import ij.measure.ResultsTable

/**
  * Created by james on 5/05/16.
  */
/**
  * @author james
  */

class CellResultCollection(condition:String,results:Seq[Result]){
  // Corresponds to all of the measurements made on a single cell.
  val area = results.head.getArea
  def writeResult(table:ResultsTable): Unit ={
    //Writes the results to an ImageJ result table.
    table.incrementCounter()
    table.addValue("ExperimentalCondition",condition)
    table.addValue("Area",results.head.getArea)
    results.map(_.writeResult(table))

  }
  def printResults: Unit ={
    println("CONDITION: " + condition)
    println("AREA: " + area)
    results.map{ r=>
      println(r.column_name + ": " + r.scaledMean)
    }

  }
}


class Result(compartment:String,condition:String,entries:Array[ResultEntry]) {
  //This class corresponds to the results from a single measurement type made on a single compartment of a single cell.
  val getCompartment = compartment
  val getCondition = condition
  val getEntries = entries
  val getArea = entries.map(_.getArea).sum
  def column_name = entries.head.getLabel

  def scaledMean:String = {
    val valid_measurements = entries.filter(_.getValue.isDefined)
    if (valid_measurements.length == 0) "NA"
    else {
      val total_area = valid_measurements.map(e => e.getArea).sum
      val scaled_measurements = valid_measurements.map {
        e => e.getValue.get * e.getArea
      }.sum
      (scaled_measurements/total_area).toString
    }
  }
  def writeResult(table:ResultsTable): Unit ={
    table.addValue(column_name,scaledMean)
  }
  override def hashCode = 31*getArea + entries.map(e=>e.hashCode).sum
  override def equals(other:Any) = other match {
    case that:Result => this.getArea == that.getArea && this.entries == that.getEntries
    case _ => false
  }
}


class ResultEntry(compartment:String,label:String,value:Option[Double],area:Int){
  //This class corresponds to the result from a single measurement on a single slice of a single cell.
  def getCompartment = compartment
  def getLabel = label //eg Compartment1IntensityChannel2
  def getArea = area
  def isPresent = getValue.isDefined
  def getValue = value match{
    case Some(x) if x.isNaN => None
    case Some(x) => Some(x)
    case None => None
  }
  override def hashCode = 31 * (31+value.hashCode) + label.hashCode + compartment.hashCode
  override def equals(other:Any) = other match{
    case that:ResultEntry => this.label == that.getLabel && this.value == that.getValue && that.getCompartment == this.compartment
    case _ => false
  }
}


