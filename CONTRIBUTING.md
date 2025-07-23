# Contributing to Z+ Management Platform

Thank you for your interest in contributing to the Z+ Management Platform! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Guidelines](#contributing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Code Standards](#code-standards)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

## Code of Conduct

This project adheres to a code of conduct that promotes respect, inclusivity, and professionalism. By participating, you agree to uphold these standards.

### Our Standards

- **Be respectful**: Treat all community members with respect and kindness
- **Be inclusive**: Welcome newcomers and support diversity
- **Be professional**: Maintain professional communication in all interactions
- **Be constructive**: Provide helpful feedback and suggestions

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 12+
- Git
- Docker (optional, for containerized development)

### Development Setup

1. **Fork the repository**
   ```bash
   git clone https://github.com/your-username/z-plus-management.git
   cd z-plus-management
   ```

2. **Set up the development environment**
   ```bash
   # Copy environment template
   cp .env.template .env
   
   # Configure your database settings in .env
   # DB_HOST=localhost
   # DB_PORT=5432
   # DB_NAME=zplus_dev
   # DB_USERNAME=your_username
   # DB_PASSWORD=your_password
   ```

3. **Set up the database**
   ```bash
   # Using the provided script
   chmod +x admin-panel/backend/setup-database.sh
   ./admin-panel/backend/setup-database.sh
   ```

4. **Build and run the application**
   ```bash
   # Build the project
   chmod +x build.sh
   ./build.sh
   
   # Run in development mode
   cd admin-panel/backend
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

## Contributing Guidelines

### Branch Naming Convention

- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `hotfix/description` - Critical fixes for production
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring

### Commit Message Format

Follow the conventional commit format:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(auth): add JWT token refresh functionality
fix(contact): resolve contact sharing permission issue
docs(api): update API documentation for new endpoints
```

### Issue Guidelines

When creating issues:

1. **Use descriptive titles**
2. **Provide detailed descriptions**
3. **Include steps to reproduce** (for bugs)
4. **Add relevant labels**
5. **Mention related issues** if applicable

**Bug Report Template:**
```markdown
## Bug Description
Brief description of the bug

## Steps to Reproduce
1. Step one
2. Step two
3. Step three

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g., Windows 10, Ubuntu 20.04]
- Java Version: [e.g., 17.0.2]
- Browser: [e.g., Chrome 96]

## Additional Context
Any other relevant information
```

## Pull Request Process

### Before Submitting

1. **Ensure your branch is up to date**
   ```bash
   git checkout main
   git pull origin main
   git checkout your-branch
   git rebase main
   ```

2. **Run tests and checks**
   ```bash
   # Run unit tests
   cd admin-panel/backend
   mvn test
   
   # Run integration tests
   mvn verify
   
   # Check code formatting
   mvn spotless:check
   ```

3. **Update documentation** if necessary

### Pull Request Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests pass locally
- [ ] No new warnings introduced
```

### Review Process

1. **Automated checks** must pass
2. **At least one reviewer** approval required
3. **No unresolved conversations**
4. **Up-to-date with main branch**

## Code Standards

### Java Code Style

- **Follow Google Java Style Guide**
- **Use meaningful variable and method names**
- **Add Javadoc for public methods**
- **Keep methods small and focused**
- **Use appropriate design patterns**

**Example:**
```java
/**
 * Retrieves contact inquiries with pagination and filtering.
 *
 * @param pageable the pagination information
 * @param searchTerm optional search term for filtering
 * @return page of contact inquiries
 */
@GetMapping("/contacts")
public ResponseEntity<Page<ContactInquiry>> getContacts(
        Pageable pageable,
        @RequestParam(required = false) String searchTerm) {
    // Implementation
}
```

### JavaScript Code Style

- **Use ES6+ features**
- **Consistent indentation (2 spaces)**
- **Meaningful function names**
- **Handle errors properly**
- **Add JSDoc comments for functions**

### HTML/CSS Standards

- **Semantic HTML5 elements**
- **Accessible markup (ARIA labels)**
- **Responsive design principles**
- **Consistent naming conventions**
- **CSS custom properties for theming**

## Testing Guidelines

### Unit Tests

- **Test public methods**
- **Mock external dependencies**
- **Use descriptive test names**
- **Follow AAA pattern** (Arrange, Act, Assert)

```java
@Test
void shouldCreateContactInquirySuccessfully() {
    // Arrange
    ContactInquiry inquiry = new ContactInquiry();
    inquiry.setName("John Doe");
    inquiry.setEmail("john@example.com");
    
    when(contactRepository.save(any())).thenReturn(inquiry);
    
    // Act
    ContactInquiry result = contactService.createInquiry(inquiry);
    
    // Assert
    assertThat(result.getName()).isEqualTo("John Doe");
    verify(contactRepository).save(inquiry);
}
```

### Integration Tests

- **Test API endpoints**
- **Use test database**
- **Test authentication flows**
- **Verify database operations**

### Frontend Tests

- **Unit tests for utilities**
- **Integration tests for components**
- **End-to-end tests for critical flows**

## Documentation

### Code Documentation

- **Javadoc for all public APIs**
- **README files for modules**
- **Inline comments for complex logic**
- **API documentation updates**

### User Documentation

- **Update user guides** when adding features
- **Include screenshots** for UI changes
- **Provide examples** for new APIs
- **Keep installation guides current**

## Development Workflow

### Local Development

1. **Start database**
   ```bash
   docker-compose up postgres
   ```

2. **Run application in dev mode**
   ```bash
   cd admin-panel/backend
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. **Hot reload for frontend**
   - Use browser dev tools
   - Live reload extensions
   - Watch file changes

### Debugging

- **Use IDE debugger** for step-through debugging
- **Enable debug logging** in application.yml
- **Use browser dev tools** for frontend issues
- **Check application logs** in logs/application.log

### Database Migrations

When making database changes:

1. **Create migration script** in `src/main/resources/db/migration/`
2. **Follow naming convention**: `V{version}__{description}.sql`
3. **Test migration** on clean database
4. **Document breaking changes**

## Release Process

### Version Numbering

Follow semantic versioning (SemVer):
- **MAJOR.MINOR.PATCH**
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

### Release Checklist

- [ ] All tests passing
- [ ] Documentation updated
- [ ] Version numbers updated
- [ ] Changelog updated
- [ ] Security scan completed
- [ ] Performance testing done

## Getting Help

### Communication Channels

- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and ideas
- **Wiki**: Detailed documentation and guides

### Maintainers

- Review pull requests
- Triage issues
- Maintain project roadmap
- Ensure code quality

## Recognition

Contributors are recognized through:
- **Contributors file** maintenance
- **Release notes** acknowledgments
- **GitHub contributions** graph

---

**Thank you for contributing to Z+ Management Platform!** 

Your contributions help make this project better for everyone. If you have questions or need help getting started, don't hesitate to reach out through GitHub issues or discussions.
