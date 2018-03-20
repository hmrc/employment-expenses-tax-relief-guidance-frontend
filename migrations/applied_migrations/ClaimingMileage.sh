#!/bin/bash

echo "Applying migration ClaimingMileage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /claimingMileage                       controllers.ClaimingMileageController.onPageLoad()" >> ../conf/app.routes
echo "POST       /claimingMileage                       controllers.ClaimingMileageController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimingMileage.title = claimingMileage" >> ../conf/messages.en
echo "claimingMileage.heading = claimingMileage" >> ../conf/messages.en
echo "claimingMileage.error.required = Please give an answer for claimingMileage" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def claimingMileage: Option[Boolean] = cacheMap.getEntry[Boolean](ClaimingMileageId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ClaimingMileage completed"
