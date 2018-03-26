#!/bin/bash

echo "Applying migration WillNotPayTax"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /willNotPayTax                       controllers.WillNotPayTaxController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "willNotPayTax.title = willNotPayTax" >> ../conf/messages.en
echo "willNotPayTax.heading = willNotPayTax" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WillNotPayTax completed"
