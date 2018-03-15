#!/bin/bash

echo "Applying migration PaidTaxInRelevantYear"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /paidTaxInRelevantYear                       controllers.PaidTaxInRelevantYearController.onPageLoad()" >> ../conf/app.routes
echo "POST       /paidTaxInRelevantYear                       controllers.PaidTaxInRelevantYearController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "paidTaxInRelevantYear.title = paidTaxInRelevantYear" >> ../conf/messages.en
echo "paidTaxInRelevantYear.heading = paidTaxInRelevantYear" >> ../conf/messages.en
echo "paidTaxInRelevantYear.error.required = Please give an answer for paidTaxInRelevantYear" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def paidTaxInRelevantYear: Option[Boolean] = cacheMap.getEntry[Boolean](PaidTaxInRelevantYearId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PaidTaxInRelevantYear completed"
