#!/bin/bash

echo "Applying migration TaxYears"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /taxYears               controllers.TaxYearsController.onPageLoad()" >> ../conf/app.routes
echo "POST       /taxYears               controllers.TaxYearsController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "taxYears.title = What tax years are you claiming for?" >> ../conf/messages.en
echo "taxYears.heading = What tax years are you claiming for?" >> ../conf/messages.en
echo "taxYears.thisYear = This Year" >> ../conf/messages.en
echo "taxYears.lastYear = Last Year" >> ../conf/messages.en
echo "taxYears.error.required = Please give an answer for taxYears" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def taxYears: Option[TaxYears] = cacheMap.getEntry[TaxYears](TaxYearsId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration TaxYears completed"
