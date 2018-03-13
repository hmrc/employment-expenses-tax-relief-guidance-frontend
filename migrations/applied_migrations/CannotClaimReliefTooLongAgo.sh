#!/bin/bash

echo "Applying migration CannotClaimReliefTooLongAgo"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotClaimReliefTooLongAgo                       controllers.CannotClaimReliefTooLongAgoController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotClaimReliefTooLongAgo.title = cannotClaimReliefTooLongAgo" >> ../conf/messages.en
echo "cannotClaimReliefTooLongAgo.heading = cannotClaimReliefTooLongAgo" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CannotClaimReliefTooLongAgo completed"
