#!/bin/bash

echo "Applying migration HowManyYearsWasTaxPaid"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howManyYearsWasTaxPaid               controllers.HowManyYearsWasTaxPaidController.onPageLoad()" >> ../conf/app.routes
echo "POST       /howManyYearsWasTaxPaid               controllers.HowManyYearsWasTaxPaidController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howManyYearsWasTaxPaid.title = In how many of the years you are claiming did you pay tax?" >> ../conf/messages.en
echo "howManyYearsWasTaxPaid.heading = In how many of the years you are claiming did you pay tax?" >> ../conf/messages.en
echo "howManyYearsWasTaxPaid.all = All" >> ../conf/messages.en
echo "howManyYearsWasTaxPaid.some = Some" >> ../conf/messages.en
echo "howManyYearsWasTaxPaid.error.required = Please give an answer for howManyYearsWasTaxPaid" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def howManyYearsWasTaxPaid: Option[HowManyYearsWasTaxPaid] = cacheMap.getEntry[HowManyYearsWasTaxPaid](HowManyYearsWasTaxPaidId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HowManyYearsWasTaxPaid completed"
