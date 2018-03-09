#!/bin/bash

echo "Applying migration Claimant"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /claimant               controllers.ClaimantController.onPageLoad()" >> ../conf/app.routes
echo "POST       /claimant               controllers.ClaimantController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimant.title = Are you claiming expecses for yourself?" >> ../conf/messages.en
echo "claimant.heading = Are you claiming expecses for yourself?" >> ../conf/messages.en
echo "claimant.you = Yes" >> ../conf/messages.en
echo "claimant.someoneElse = No" >> ../conf/messages.en
echo "claimant.error.required = Please give an answer for claimant" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def claimant: Option[Claimant] = cacheMap.getEntry[Claimant](ClaimantId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration Claimant completed"
