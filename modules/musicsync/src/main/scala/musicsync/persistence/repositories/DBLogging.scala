package musicsync.persistence.repositories

import monix.eval.Task

trait DBLogging {

  implicit class ImproveLogging[A](task: Task[A]) {

    def improveLogging(queryName: String): Task[A] = task.onErrorHandleWith { error: Throwable =>
      Task.raiseError(new Exception(s"Error at query: $queryName", error))
    }
  }
}
