package models

import play.api.libs.json.{JsSuccess, JsValue, Reads}

case class Tweet(from: String, text: String)

object Tweet {

  implicit object TweetReads extends Reads[Tweet] {
    def reads(json: JsValue): JsSuccess[Tweet] = JsSuccess( Tweet (
      (json \ "from_user_name").as[String],
      (json \ "text").as[String]
    ))
  }

}




