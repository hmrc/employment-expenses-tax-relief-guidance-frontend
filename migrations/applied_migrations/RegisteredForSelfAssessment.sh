#!/bin/bash

echo "Applying migration RegisteredForSelfAssessment"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /registeredForSelfAssessment                       controllers.RegisteredForSelfAssessmentController.onPageLoad()" >> ../conf/app.routes
echo "POST       /registeredForSelfAssessment                       controllers.RegisteredForSelfAssessmentController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registeredForSelfAssessment.title = registeredForSelfAssessment" >> ../conf/messages.en
echo "registeredForSelfAssessment.heading = registeredForSelfAssessment" >> ../conf/messages.en
echo "registeredForSelfAssessment.error.required = Please give an answer for registeredForSelfAssessment" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def registeredForSelfAssessment: Option[Boolean] = cacheMap.getEntry[Boolean](RegisteredForSelfAssessmentId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredForSelfAssessment completed"
