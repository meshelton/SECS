package io.github.meshelton.secs

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
  * A Component
  * 
  * Represents a component. Not only must there be a class that implements a desired component but also a corresponding subclass of [[io.github.meshelton.secs.ComponentManager ComponentManager]]
  * Components should only store mutable data and only have getter and setter methods
  */
trait Component

/**
  * Manages a type of componenet
  * 
  * Manages the components in the system of [[io.github.meshelton.secs.ComponentManager.TypeOfComponent TypeOfComponent]]
  * Should be used when attempting to retrieve entities that have a specific component
  * TypeOfComponent needs to be set to the type of the managed component when subclassing this
  * [[io.github.meshelton.secs.ComponentManager.buildComponent buildComponent]] needs to be set defined as creating a new instance of the given component when subclassing this
  * Entities can only have a single componenet of a given type attached to them
  * @constructor Creates a new ComponentManager registered to a EntityManager
  * @param entityManager The [[io.github.meshelton.secs.EntityManager EntityManager]] that this component manager will be registered to
  */
abstract class ComponentManager(val entityManager: EntityManager) {
  /**
    * The type of component that this ComponentManager manages
    */
  type TypeOfComponent <: Component

  entityManager.registerComponentManager(this)

  private val components = HashMap[Entity, TypeOfComponent]()

  /**
    * Gets the component that is attached to entity
    * 
    * @param entity the entity that may or may not have an attached component
    * @return an option containing the component attached to the entity
    */
  def apply(entity: Entity): Option[TypeOfComponent] = getComponent(entity)

  /**
    * Creates and adds a new component to the entity
    * 
    * This is the only way new components can be made and added to the overall system
    * 
    * @param entity the entity that the new component will be attached to
    * @return the newly created component
    */
  def addComponent(entity: Entity): TypeOfComponent = {
    val newComponent = buildComponent()
    components(entity) = newComponent
    newComponent
  }

  /**
    * Used internally to create a new instance of TypeOfComponent
    * 
    * @return the newly created component
    */
  def buildComponent(): TypeOfComponent

  /**
    * Removes the component from the entity
    * 
    * @param entity the entity from which to remove the component
    */
  def removeComponent(entity: Entity) = {
    components -= entity
  }

  /**
    * Gets the component that is attached to entity
    * 
    * @param entity the entity that may or may not have an attached component
    * @return an option containing the component attached to the entity
    */
  def getComponent(entity: Entity): Option[TypeOfComponent] = {
    components.get(entity)
  }

  /**
    * Gets all the components managed by this ComponentManager
    * 
    * @return all the components managed by this ComponentManager
    */
  def getComponents(): ArrayBuffer[TypeOfComponent] = {
    ArrayBuffer(components.valuesIterator.toSeq: _*)
  }

  /**
    * Gets all the entities that have components managed by this ComponentManager
    * 
    * @return all the entities that have components managed by this ComponentManager
    */
  def getEntities(): ArrayBuffer[Entity] = {
    ArrayBuffer(components.keySet.toSeq: _*)
  }
}
