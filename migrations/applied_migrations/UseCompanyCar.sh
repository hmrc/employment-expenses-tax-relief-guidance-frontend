#!/bin/bash

echo "Applying migration UseCompanyCar"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /useCompanyCar                       controllers.UseCompanyCarController.onPageLoad()" >> ../conf/app.routes
echo "POST       /useCompanyCar                       controllers.UseCompanyCarController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "useCompanyCar.title = useCompanyCar" >> ../conf/messages.en
echo "useCompanyCar.heading = useCompanyCar" >> ../conf/messages.en
echo "useCompanyCar.error.required = Please give an answer for useCompanyCar" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def useCompanyCar: Option[Boolean] = cacheMap.getEntry[Boolean](UseCompanyCarId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UseCompanyCar completed"
