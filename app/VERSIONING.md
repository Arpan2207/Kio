# Android Version Support

## Current Version Targets

- **Minimum SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 34
- **Compile SDK:** 35

## Development Decisions

The project currently targets Android 8.0+ devices to utilize modern Android features including:

- Adaptive icons
- Runtime permissions model
- JobScheduler API
- Modern UI components

## Future Considerations

If broader device support is needed in the future, consider:

1. Using version-specific resource directories (e.g., mipmap-v26 for adaptive icons)
2. Implementing fallbacks for devices below API 26 
3. Using the Support/AndroidX libraries for backward compatibility

## Versioning Checklist

When adding new features, check:

- [ ] Feature compatibility with minSdk 26
- [ ] Appropriate use of AndroidX components
- [ ] Version-specific code is properly guarded with SDK_INT checks
- [ ] Icons and drawables follow the adaptive icon pattern

Last updated: March 28, 2025 