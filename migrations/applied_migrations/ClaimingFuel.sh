#!/bin/bash

echo "Applying migration ClaimingFuel"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /claimingFuel                       controllers.ClaimingFuelController.onPageLoad()" >> ../conf/app.routes
echo "POST       /claimingFuel                       controllers.ClaimingFuelController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimingFuel.title = claimingFuel" >> ../conf/messages.en
echo "claimingFuel.heading = claimingFuel" >> ../conf/messages.en
echo "claimingFuel.error.required = Please give an answer for claimingFuel" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def claimingFuel: Option[Boolean] = cacheMap.getEntry[Boolean](ClaimingFuelId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ClaimingFuel completed"
