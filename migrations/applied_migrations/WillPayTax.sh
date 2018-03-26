#!/bin/bash

echo "Applying migration WillPayTax"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /willPayTax                       controllers.WillPayTaxController.onPageLoad()" >> ../conf/app.routes
echo "POST       /willPayTax                       controllers.WillPayTaxController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "willPayTax.title = willPayTax" >> ../conf/messages.en
echo "willPayTax.heading = willPayTax" >> ../conf/messages.en
echo "willPayTax.error.required = Please give an answer for willPayTax" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def willPayTax: Option[Boolean] = cacheMap.getEntry[Boolean](WillPayTaxId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WillPayTax completed"
