package views

import play.api.data.Form
import controllers.routes
import forms.$className$FormProvider
import models.$className$
import views.behaviours.QuestionViewBehaviours
import views.html.$className;format="decap"$

class $className$ViewSpec extends QuestionViewBehaviours[$className$] {

  val messageKeyPrefix = "$className;format="decap"$"

  override val form = new $className$FormProvider()()

  def createView = () => $className;format="decap"$(frontendAppConfig, form)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => $className;format="decap"$(frontendAppConfig, form)(fakeRequest, messages)


  "$className$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.$className$Controller.onSubmit().url, "field1", "field2")
  }
}
