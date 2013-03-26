package com.softwaremill.codebrag.rest

import com.softwaremill.codebrag.service.user.Authenticator
import com.softwaremill.codebrag.AuthenticatableServletSpec
import org.scalatra.auth.Scentry
import com.softwaremill.codebrag.service.data.UserJson
import com.softwaremill.codebrag.dao.CommitInfoDAO
import org.mockito.Mockito._
import com.softwaremill.codebrag.dao.reporting.{CommitListDTO, CommitListFinder, CommitListItemDTO}
import java.util.Date


class CommitsServletSpec extends AuthenticatableServletSpec {

  val SamplePendingCommits = CommitListDTO(List(CommitListItemDTO("abcd0123", "this is commit message", "mostr", "michal", new Date())))

  var commitsInfoDao = mock[CommitInfoDAO]
  var commitsListFinder = mock[CommitListFinder]

  def bindServlet = addServlet(new TestableCommitsServlet(commitsInfoDao, fakeAuthenticator, fakeScentry), "/*")

  "GET /commits" should "respond with HTTP 401 when user is not authenticated" in {
    userIsNotAuthenticated
    get("/") {
      status should be (401)
    }
  }

  "GET /commits" should "respond with HTTP 404 (not yet done) when user is authenticated" in {
    userIsAuthenticated
    get("/") {
      status should be (404)
    }
  }

  "GET /commits?type=pending" should "should return commits pending review" in {
    userIsAuthenticated
    when(commitsListFinder.findAllPendingCommits()).thenReturn(SamplePendingCommits)
    get("/?type=pending") {
      status should be (200)
      body should equal(asJson(SamplePendingCommits))
    }

    def asJson(resp: CommitListDTO) = {
      implicit val formats = net.liftweb.json.DefaultFormats ++ net.liftweb.json.ext.JodaTimeSerializers.all
      net.liftweb.json.Serialization.write(resp)
    }
  }

  class TestableCommitsServlet(commitInfoDao: CommitInfoDAO, fakeAuthenticator: Authenticator, fakeScentry: Scentry[UserJson])
    extends CommitsServlet(fakeAuthenticator, commitInfoDao, commitsListFinder, new CodebragSwagger) {
    override def scentry(implicit request: javax.servlet.http.HttpServletRequest) = fakeScentry
  }

}