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

  entity1.add(PositionComponent(5, 100))
    .add(VelocityComponent(0, 0))
    .add(GravityComponent())

  println()

  println("Entity 2 at position (5, 5) with initial velocity (1, 1)")
  val entity2 = Entity.create
  entity2.add(PositionComponent(5, 5))
    .add(VelocityComponent(1, 1))

  println("Entity 3 subject to gravity")
  val entity3 = Entity.create
  entity3.add(GravityComponent())

  for( x <- 0 to 10 ){
    entityManager.update(0.3f)

    println("After a 1 second update")

    entity1.get[PositionComponent].foreach{ posComp => 
      println(s"Entity1 at Pos(${posComp.x}, ${posComp.y})")
    }

    entity2.get[PositionComponent].foreach{ posComp => 
      println(s"Entity2 at Pos(${posComp.x}, ${posComp.y})")
    }

    entity3.get[PositionComponent].foreach{ posComp => 
      println(s"Entity3 at Pos(${posComp.x}, ${posComp.y})")
    }
  }
}

class GravitySystem(val accel: Float)
                   (implicit val gravCompManager: ComponentManager[GravityComponent],
                    val velCompManager: ComponentManager[VelocityComponent],
                    val entityManager: EntityManager ) extends System {

  def update(delta: Float) = {
    val entities = gravCompManager.getEntities()
    entities.foreach{ entity => {
        entity.get[VelocityComponent].foreach( _.y += accel * delta )
      }
    }
  }

}

class PositionSystem(implicit val velCompManager: ComponentManager[VelocityComponent],
                     val posCompManager: ComponentManager[PositionComponent],
                     val entityManager: EntityManager ) extends System {

  def update(delta: Float) = {
    val entities = posCompManager() intersect velCompManager()
    entities.foreach(
      (entity) => {
        for( posComp <- entity.get[PositionComponent];
             velComp <- entity.get[VelocityComponent] ){
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
