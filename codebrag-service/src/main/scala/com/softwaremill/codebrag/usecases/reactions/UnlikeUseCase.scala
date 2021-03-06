package com.softwaremill.codebrag.usecases.reactions

import com.softwaremill.codebrag.service.comments.{UserReactionService, LikeValidator}
import org.bson.types.ObjectId
import com.softwaremill.codebrag.domain.User

class UnlikeUseCase(likeValidator: LikeValidator, userReactionService: UserReactionService) {

  type UnlikeResult = Either[String, Unit]

  def execute(currentUser: User, likeId: ObjectId): Either[String, Unit] = {
    ifCanExecute(currentUser.id, likeId) {
      userReactionService.removeLike(likeId)
      Right()
    }
  }

  protected def ifCanExecute(userId: ObjectId, likeId: ObjectId)(block: => UnlikeResult): UnlikeResult = {
    likeValidator.canUserDoUnlike(userId, likeId) match  {
      case Right(_) => block
      case Left(err) => Left(err)
    }
  }

}