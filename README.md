# Test assignment for Android Engineer

One screen Android app for tracking Ethereum balances and transfers.

## Features

- **Part A:** detect and display the latest 50 *ERC-20* incoming token transfers (
  e.g. [USDC token](https://etherscan.io/token/0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48)) on the
  Ethereum network sent to the address below.
- **Part B:** determine and display the current ETH balance on the Ethereum network of the address
  below.
- **Part C:** determine and display the current token balance of *ERC-20* token (
  e.g. [USDC token](https://etherscan.io/token/0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48)) on the
  Ethereum network of the address below.

## Solution

### Architecture

- Gradle multi-module. Layers: app -> feature -> data -> core
- Kotlin Multiplatform. Modules are ready for reusing: feature -> data -> core
- Jetpack Compose and Material You
- Decompose, Kotlin multiplatform library, for BLoC components
- SqlDelight for app database
- DataStore and wire for protobuf storage
- Efficient and Kotlin friendly implementation of Web3 stack (`:core:web3`)

### Implementation details

- Account TextField to change active account
- 1inch and rainbow tokenlists for token balance discovery
- **Part
  A:** [`detectNewIncomingERC20Transfers`](https://github.com/nailkhaf/Tokens-Feed/blob/c8067b54677df5ec6b12af17e15a25b68b59311d/data/tokens/src/commonMain/kotlin/DefaultERC20TransfersRepository.kt)
- **Part B** and **Part
  C:** [`detectNewERC20Tokens`](https://github.com/nailkhaf/Tokens-Feed/blob/c8067b54677df5ec6b12af17e15a25b68b59311d/data/tokens/src/commonMain/kotlin/DefaultERC20TokensRepository.kt)

## How to install

run:

- `./gradlew generateCommonMainDatabaseInterface`
- `./gradlew generateCommonCommonMainProtos`
- `./gradlew installDebug`
