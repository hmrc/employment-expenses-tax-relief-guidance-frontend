#!/bin/bash

echo "Applying migration ClaimingFor"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /claimingFor               controllers.ClaimingForController.onPageLoad()" >> ../conf/app.routes
echo "POST       /claimingFor               controllers.ClaimingForController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimingFor.title = What are you claiming for?" >> ../conf/messages.en
echo "claimingFor.heading = What are you claiming for?" >> ../conf/messages.en
echo "claimingFor.uniformsClothingTools = Uniforms, work clothing and tools" >> ../conf/messages.en
echo "claimingFor.mileageFuel = Business mileage, fuel and electricity costs" >> ../conf/messages.en
echo "claimingFor.error.required = Please give an answer for claimingFor" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def claimingFor: Option[ClaimingFor] = cacheMap.getEntry[ClaimingFor](ClaimingForId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ClaimingFor completed"
