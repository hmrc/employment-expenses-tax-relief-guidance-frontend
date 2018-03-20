#!/bin/bash

echo "Applying migration CannotClaimBuyingEquipment"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotClaimBuyingEquipment                       controllers.CannotClaimBuyingEquipmentController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotClaimBuyingEquipment.title = cannotClaimBuyingEquipment" >> ../conf/messages.en
echo "cannotClaimBuyingEquipment.heading = cannotClaimBuyingEquipment" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CannotClaimBuyingEquipment completed"
