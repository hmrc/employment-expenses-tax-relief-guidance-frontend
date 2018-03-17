#!/bin/bash

echo "Applying migration CannotClaimMileageCosts"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotClaimMileageCosts                       controllers.CannotClaimMileageCostsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotClaimMileageCosts.title = cannotClaimMileageCosts" >> ../conf/messages.en
echo "cannotClaimMileageCosts.heading = cannotClaimMileageCosts" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CannotClaimMileageCosts completed"
