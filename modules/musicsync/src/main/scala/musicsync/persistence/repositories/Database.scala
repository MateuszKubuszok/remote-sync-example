package musicsync.persistence.repositories

import cats.effect.{ Blocker, Resource }
import doobie.Transactor
import doobie.util.ExecutionContexts
import monix.eval.Task
import org.flywaydb.core.Flyway

class Database(url: String, user: String, password: String) {

  def migrate: Task[Unit] = Task {
    Flyway.configure().dataSource(url, user, password).load().migrate()
    ()
  }

  def makeTransactor: Resource[Task, Transactor[Task]] =
    ExecutionContexts.cachedThreadPool[Task].map(Blocker.liftExecutionContext).map { blocker =>
      Transactor.fromDriverManager[Task](
        driver = "org.postgresql.Driver",
        url    = url,
        user   = user,
        pass   = password,
        blocker
      )
    }
}
object Database {

  final case class Config(url: String, user: String, password: String)

  def apply(config: Config): Database = new Database(config.url, config.user, config.password)
}
