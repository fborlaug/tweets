package controllers

import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import models.Tweet
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.concurrent.Promise

object Application extends Controller {
  var counter: Long = 1;

  def tweetList() = Action {
    val results = 3
    val query = "norsk"
    val responseFuture =
      WS.url("http://search.twitter.com/search.json")
        .withQueryString("q" -> query, "rpp" -> results.toString)
        .get()

    val resultFuture = responseFuture.map {
      response =>
        response.status match {
          case 200 => Some(response.body)
          case _ => None
        }
    }

    val result = Await.result(resultFuture, Duration(5, "seconds")).getOrElse("")
    val tweets = Json.parse(result).\("results").as[Seq[Tweet]]

    Ok(views.html.twitterrest.tweetlist(tweets))
  }

  def websocketServer() = WebSocket.using[String] {
    implicit request =>
      val in = Iteratee.ignore[String]
      val out = Enumerator.generateM(
        Promise.timeout(Some(counter.toString), Duration(5, "seconds"))
      )
      counter+=1L
      (in, out)
  }

  def websocketClient() = Action {
    implicit request =>
      Ok(views.html.websocketclient(request))
  }
}

