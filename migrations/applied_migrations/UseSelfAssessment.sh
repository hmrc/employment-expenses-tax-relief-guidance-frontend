#!/bin/bash

echo "Applying migration UseSelfAssessment"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /useSelfAssessment                       controllers.UseSelfAssessmentController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "useSelfAssessment.title = useSelfAssessment" >> ../conf/messages.en
echo "useSelfAssessment.heading = useSelfAssessment" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UseSelfAssessment completed"
