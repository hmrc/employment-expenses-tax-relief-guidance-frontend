#!/bin/bash

echo "Applying migration UsePrintAndPost"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /usePrintAndPost                       controllers.UsePrintAndPostController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "usePrintAndPost.title = usePrintAndPost" >> ../conf/messages.en
echo "usePrintAndPost.heading = usePrintAndPost" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UsePrintAndPost completed"
