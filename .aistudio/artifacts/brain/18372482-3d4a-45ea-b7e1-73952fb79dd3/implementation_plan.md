# Implementation Plan: Game Transition to 3D

## 1. Goal
Transition existing 2D games to 3D using SceneView for Android (Jetpack Compose). Integrate the Keon chatbot fully.

## 2. Approach
- Introduce SceneView dependency in `build.gradle.kts`.
- Refactor `GamesTab.kt` to act as a container for 3D game previews.
- Create a `WolfGame` 3D implementation.
- Refactor `MinecraftVoxelGame.kt` to use `SceneView` and higher-quality textures.
- Integrate Keon chatbot via a `ChatbotSystem` class that manages interactions across the application.
- Clean up unused files.

## 3. Scope & Feasibility
- 3D transition is complex. We will implement a 3D foundation first.
- Chatbot integration will use `gemini-api` skill.

## 4. Feedback Needed
- Should we focus on one game first for the 3D transition (e.g., the wolf game) or refactor everything at once?
- Do you have specific models/assets for the wolf game? (I will use generic 3D shapes/materials for now if not).
