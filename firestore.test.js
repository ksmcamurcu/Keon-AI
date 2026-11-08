const assert = require('assert');
const { initializeTestEnvironment, assertFails, assertSucceeds } = require('@firebase/rules-unit-testing');
const fs = require('fs');

async function runTests() {
    const testEnv = await initializeTestEnvironment({
        projectId: 'gen-lang-client-0625815123',
        firestore: { rules: fs.readFileSync('firestore.rules', 'utf8') }
    });

    try {
        console.log('Running test: should prevent unauthenticated access');
        const unauthedDb = testEnv.unauthenticatedContext().firestore();
        await assertFails(unauthedDb.collection('users').doc('user1').get());
        console.log('PASS');

        console.log('Running test: should allow user to read their own profile');
        const authedDb = testEnv.authenticatedContext('user1').firestore();
        await assertSucceeds(authedDb.collection('users').doc('user1').get());
        console.log('PASS');

        console.log('Running test: should prevent user from reading other profiles');
        const authedDb2 = testEnv.authenticatedContext('user1').firestore();
        await assertFails(authedDb2.collection('users').doc('user2').get());
        console.log('PASS');
        
    } finally {
        await testEnv.cleanup();
    }
}

runTests().catch(console.error);
