package com.softwaremill.codebrag.rest

import com.softwaremill.codebrag.AuthenticatableServletSpec
import com.softwaremill.codebrag.dao.reporting.NotificationCountFinder
import com.softwaremill.codebrag.service.data.UserJson
import org.mockito.BDDMockito._
import org.mockito.Matchers._
import com.softwaremill.codebrag.dao.reporting.views.NotificationCountersView
import org.bson.types.ObjectId
import com.softwaremill.codebrag.domain.{Authentication, User}

class UpdatesServletSpec extends AuthenticatableServletSpec {
  val countersFinderMock: NotificationCountFinder = mock[NotificationCountFinder]
  val user = currentUser(new ObjectId)

  override def beforeEach {
    super.beforeEach
    addServlet(new TestableUpdatesServlet(countersFinderMock), "/*")
  }

  "GET /" should "respond with HTTP 401 when user is not authenticated" in {
    userIsNotAuthenticated
    get("/") {
      status should be(401)
    }
  }

  "GET /" should "call finder to fetch counters for authorized user" in {
    // given
    userIsAuthenticatedAs(UserJson(user))
    val expectedCommits = 1
    val expectedFollowups = 2
    given(countersFinderMock.getCounters(any[ObjectId])).willReturn(NotificationCountersView(expectedCommits, expectedFollowups))

    // when
    get("/") {
      //then
      status should equal(200)
      body should include( s""""commits":$expectedCommits""")
      body should include( s""""followups":$expectedFollowups""")
    }
  }

  def currentUser(id: ObjectId) = User(id, Authentication.basic("user", "password"), "John Doe", "john@doe.com", "abcde", "avatarUrl")

  class TestableUpdatesServlet(finder: NotificationCountFinder) extends UpdatesServlet(fakeAuthenticator, finder) {
    override def scentry(implicit request: javax.servlet.http.HttpServletRequest) = fakeScentry
  }

}

