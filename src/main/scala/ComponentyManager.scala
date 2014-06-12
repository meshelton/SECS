package com.shelton

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, HashMap}

abstract class ComponentManager(val entityManager: EntityManager) {
  trait Component
  type T <: Component

  entityManager.registerComponentManager(this)

  val components = HashMap[entityManager.Entity, T]()

  def addComponent(entity: entityManager.Entity): T = {
    val newComponent = buildComponent()
    components(entity) = newComponent
    newComponent
  }

  protected def buildComponent(): T 

  def removeComponent(entity: entityManager.Entity) = {
    components -= entity
  }

  def getComponent(entity: entityManager.Entity): Option[T] = {
    components.get(entity)
  }

  def getEntities(): ArrayBuffer[entityManager.Entity] = {
    ArrayBuffer(components.keySet.toSeq: _*)
  }

  def removeEntity(entity: entityManager.Entity) = {
    components -= entity
  }

}


