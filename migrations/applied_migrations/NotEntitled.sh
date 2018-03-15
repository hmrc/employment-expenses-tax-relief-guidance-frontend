#!/bin/bash

echo "Applying migration NotEntitled"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /notEntitled                       controllers.NotEntitledController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "notEntitled.title = notEntitled" >> ../conf/messages.en
echo "notEntitled.heading = notEntitled" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration NotEntitled completed"
