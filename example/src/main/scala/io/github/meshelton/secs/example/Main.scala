package io.github.meshelton.secs.example
import io.github.meshelton.secs._

object Main extends App {
  // The entity manager for the application
  implicit val entityManager = new EntityManager()

  // The component managers, one for each type of component
  implicit val velCompMan = new ComponentManager[VelocityComponent]()
  implicit val posCompMan = new ComponentManager[PositionComponent]()
  implicit val gravCompMan = new ComponentManager[GravityComponent]()

  // The system that are implicitly passed the component managers they are interested in
  val gravSystem = new GravitySystem(-9.8f)
  gravSystem.updating = true
  val posSystem = new PositionSystem()
  posSystem.updating = true

  println("Entity 1 at position (5, 100) with no initial velocity and subject to gravity")
  // Creating an entity implicitly using hte entity manager
  val entity1 = Entity.create

  entity1.addComponent(PositionComponent(5, 100))
    .addComponent(VelocityComponent(0, 0))
    .addComponent(GravityComponent())

  println()

  println("Entity 2 at position (5, 5) with initial velocity (1, 1)")
  val entity2 = Entity.create
  entity2.addComponent(PositionComponent(5, 5))
    .addComponent(VelocityComponent(1, 1))

  println("Entity 3 subject to gravity")
  val entity3 = Entity.create
  entity3.addComponent(GravityComponent())

  for( x <- 0 to 10 ){
    entityManager.update(0.3f)

    println("After a 1 second update")

    entity1.getComponents[PositionComponent].foreach(
      (posComp) => println(s"Entity1 at Pos(${posComp.x}, ${posComp.y})")
    )

    entity2.getComponents[PositionComponent].foreach(
      (posComp) => println(s"Entity2 at Pos(${posComp.x}, ${posComp.y})")
    )

    entity3.getComponents[PositionComponent].foreach(
      (posComp) => println(s"Entity3 at Pos(${posComp.x}, ${posComp.y})")
    )

  }
}

class GravitySystem(val accel: Float)
                   (implicit val gravCompManager: ComponentManager[GravityComponent],
                    val velCompManager: ComponentManager[VelocityComponent],
                    em: EntityManager ) extends System {

  def update(delta: Float) = {
    val entities = gravCompManager.getEntities()
    entities.foreach(
      (entity) => {
        entity.getComponents[VelocityComponent].foreach( _.y += accel * delta)
      }
    )
  }

}

class PositionSystem(implicit val velCompManager: ComponentManager[VelocityComponent],
                     val posCompManager: ComponentManager[PositionComponent],
                     em: EntityManager ) extends System {

  def update(delta: Float) = {
    val entities = posCompManager() & velCompManager()
    entities.foreach(
      (entity) => {
        for( posComp <- entity.getComponents[PositionComponent];
             velComp <- entity.getComponents[VelocityComponent] ){
          posComp.y += velComp.y * delta
          posComp.x += velComp.x * delta
        }
      }
    )
  }

}

case class GravityComponent() extends Component

case class VelocityComponent(var x: Float, var y: Float) extends Component{
  def set(x: Float, y: Float){
    this.x = x
    this.y = y
  }
}

case class PositionComponent(var x: Float, var y: Float) extends Component {
  def moveTo(x: Float, y: Float) = {
    this.x = x
    this.y = y
  }
  def moveBy(dx: Float, dy: Float) = {
    x += dx
    y += dy
  }
  def getPoint(): (Float, Float) = (x, y)
}
