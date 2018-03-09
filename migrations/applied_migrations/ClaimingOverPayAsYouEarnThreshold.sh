#!/bin/bash

echo "Applying migration ClaimingOverPayAsYouEarnThreshold"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /claimingOverPayAsYouEarnThreshold                       controllers.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()" >> ../conf/app.routes
echo "POST       /claimingOverPayAsYouEarnThreshold                       controllers.ClaimingOverPayAsYouEarnThresholdController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimingOverPayAsYouEarnThreshold.title = claimingOverPayAsYouEarnThreshold" >> ../conf/messages.en
echo "claimingOverPayAsYouEarnThreshold.heading = claimingOverPayAsYouEarnThreshold" >> ../conf/messages.en
echo "claimingOverPayAsYouEarnThreshold.error.required = Please give an answer for claimingOverPayAsYouEarnThreshold" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def claimingOverPayAsYouEarnThreshold: Option[Boolean] = cacheMap.getEntry[Boolean](ClaimingOverPayAsYouEarnThresholdId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ClaimingOverPayAsYouEarnThreshold completed"
