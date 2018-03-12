#!/bin/bash

echo "Applying migration RegisterForSelfAssessment"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /registerForSelfAssessment                       controllers.RegisterForSelfAssessmentController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registerForSelfAssessment.title = registerForSelfAssessment" >> ../conf/messages.en
echo "registerForSelfAssessment.heading = registerForSelfAssessment" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterForSelfAssessment completed"
