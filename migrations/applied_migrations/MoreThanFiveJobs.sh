#!/bin/bash

echo "Applying migration MoreThanFiveJobs"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /moreThanFiveJobs                       controllers.MoreThanFiveJobsController.onPageLoad()" >> ../conf/app.routes
echo "POST       /moreThanFiveJobs                       controllers.MoreThanFiveJobsController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "moreThanFiveJobs.title = moreThanFiveJobs" >> ../conf/messages.en
echo "moreThanFiveJobs.heading = moreThanFiveJobs" >> ../conf/messages.en
echo "moreThanFiveJobs.error.required = Please give an answer for moreThanFiveJobs" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def moreThanFiveJobs: Option[Boolean] = cacheMap.getEntry[Boolean](MoreThanFiveJobsId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration MoreThanFiveJobs completed"
