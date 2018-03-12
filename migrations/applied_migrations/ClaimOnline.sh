#!/bin/bash

echo "Applying migration ClaimOnline"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /claimOnline                       controllers.ClaimOnlineController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimOnline.title = claimOnline" >> ../conf/messages.en
echo "claimOnline.heading = claimOnline" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ClaimOnline completed"
