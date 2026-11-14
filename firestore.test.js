const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfile(uid) {
  const now = new Date(Date.now() - 1000);
  return {
    userId: uid,
    displayName: "Ahmet",
    age: 17,
    heightCm: 176,
    startWeightKg: 68.5,
    currentWeightKg: 68.5,
    targetWeightKg: 72.0,
    goal: "growth_posture",
    environment: "home",
    activityLevel: "moderate",
    experienceLevel: "occasional",
    weeklyDays: 4,
    sessionMinutes: 40,
    focusArea: "full_body",
    planTier: "free",
    proSource: "none",
    proExpiresAtMillis: 0,
    inviteCode: "TAVUK-ALICE",
    referralCount: 0,
    referredByCode: "",
    aiQuestionsUsedToday: 0,
    aiQuotaDate: "2026-11-14",
    onboardingCompleted: true,
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) await testEnv.cleanup();
});

beforeEach(async () => {
  if (testEnv) await testEnv.clearFirestore();
});

test("Unauthenticated user cannot read or write user profiles", async () => {
  const unauth = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauth.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauth.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID))
  );
});

test("Authenticated user can create and read own profile, but not another user's", async () => {
  const alice = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bob = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    alice.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID))
  );
  await assertSucceeds(alice.collection("users").doc(ALICE_UID).get());
  await assertFails(bob.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bob.collection("users").doc(ALICE_UID).update({ planTier: "pro" })
  );
});

test("Shadow update with ghost field is rejected", async () => {
  const alice = testEnv.authenticatedContext(ALICE_UID).firestore();
  const payload = validProfile(ALICE_UID);
  payload.isAdmin = true;
  await assertFails(alice.collection("users").doc(ALICE_UID).set(payload));
});

test("Authenticated user can create and list own weightLogs and calendarEntries", async () => {
  const alice = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bob = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    alice
      .collection("users")
      .doc(ALICE_UID)
      .collection("weightLogs")
      .doc("log1")
      .set({
        weightKg: 68.2,
        note: "Sabah tartisi",
        loggedAtMillis: 1700000000000,
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    alice
      .collection("users")
      .doc(ALICE_UID)
      .collection("calendarEntries")
      .doc("entry1")
      .set({
        dateKey: "2026-11-14",
        workoutId: "youth_1",
        workoutTitle: "Boy Uzatma Mobilite",
        completed: false,
        durationMinutes: 30,
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    alice.collection("users").doc(ALICE_UID).collection("weightLogs").get()
  );
  await assertFails(
    bob.collection("users").doc(ALICE_UID).collection("weightLogs").get()
  );
});

test("Referral code creation, friend redemption, and self-referral prevention", async () => {
  const alice = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bob = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    alice.collection("referralCodes").doc("TAVUK-ALICE").set({
      ownerUid: ALICE_UID,
      code: "TAVUK-ALICE",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Alice cannot redeem her own code
  await assertFails(
    alice
      .collection("referralCodes")
      .doc("TAVUK-ALICE")
      .collection("redemptions")
      .doc(ALICE_UID)
      .set({
        code: "TAVUK-ALICE",
        codeOwnerUid: ALICE_UID,
        redeemerUid: ALICE_UID,
        createdAt: now,
      })
  );

  // Bob can redeem Alice's code
  await assertSucceeds(
    bob
      .collection("referralCodes")
      .doc("TAVUK-ALICE")
      .collection("redemptions")
      .doc(BOB_UID)
      .set({
        code: "TAVUK-ALICE",
        codeOwnerUid: ALICE_UID,
        redeemerUid: BOB_UID,
        createdAt: now,
      })
  );

  // Alice can query her redemptions with codeOwnerUid filter
  await assertSucceeds(
    alice
      .collection("referralCodes")
      .doc("TAVUK-ALICE")
      .collection("redemptions")
      .where("codeOwnerUid", "==", ALICE_UID)
      .get()
  );

  // Bob cannot list Alice's redemptions
  await assertFails(
    bob
      .collection("referralCodes")
      .doc("TAVUK-ALICE")
      .collection("redemptions")
      .get()
  );
});
