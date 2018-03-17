#!/bin/bash

echo "Applying migration CannotClaimMileageFuelCosts"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotClaimMileageFuelCosts                       controllers.CannotClaimMileageFuelCostsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotClaimMileageFuelCosts.title = cannotClaimMileageFuelCosts" >> ../conf/messages.en
echo "cannotClaimMileageFuelCosts.heading = cannotClaimMileageFuelCosts" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CannotClaimMileageFuelCosts completed"
