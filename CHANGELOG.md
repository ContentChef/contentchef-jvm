# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
## [1.3.0] - 2021-05-14
### Added
- A `Locale` can now be provided to `getPreviewChannel` and `getOnlineChannel` to retrieve localized content
### Fixed
- Fix `NullPointerException` when a 404 response happens. Now `ContentNotFoundException` it's correctly used 

## [1.2.2] - 2021-04-30
### Added
- Now available in MavenCentral
### Fixed
- Fix gradle dependencies sync

## [1.2.1] - 2021-02-04
### Changed
- Using the SDK from Java is now easier thanks to some Java/Kotlin interoperability annotations

## [1.2.0] - 2020-09-04
### Changed
- `previewApiKey` must now be provided to `getPreviewChannel()` instead of `ContentChefEnvironmentConfiguration`
- `onlineApiKey` must now be provided to `getOnlineChannel()` instead of `ContentChefEnvironmentConfiguration`

## [1.1.0] - 2020-07-10
### Changed
- `onlineApiKey` and `previewApiKey` must now be provided to `ContentChefEnvironmentConfiguration`

## [1.0.0] - 2020-01-31
### Added
- Initial version, created by ContentChef team