#!/bin/bash

echo "Applying migration CannotClaimRelief"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotClaimRelief                       controllers.CannotClaimReliefController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotClaimRelief.title = cannotClaimRelief" >> ../conf/messages.en
echo "cannotClaimRelief.heading = cannotClaimRelief" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CannotClaimRelief completed"
