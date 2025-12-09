# Process Report

## Warehouse Management System - SEP3

---

## 1. Team Organization

### 1.1 Team Members
| Name | Role | Responsibilities |
|------|------|------------------|
| Member 1 | Project Lead | Coordination, Documentation, Integration |
| Member 2 | Backend Developer | Java Server, Database Design |
| Member 3 | Microservice Developer | C# Service, gRPC Implementation |
| Member 4 | Frontend Developer | JavaFX Client, UI/UX Design |

### 1.2 Communication Channels
- **Discord**: Daily communication, quick questions
- **GitHub**: Code reviews, issue tracking
- **Weekly Meetings**: Progress updates, planning

---

## 2. Methodology: Kanban

### 2.1 Why Kanban?
We chose Kanban over Scrum because:
- More flexibility for a student project
- No rigid sprint boundaries
- Visual progress tracking
- Easy to adapt to changing requirements

### 2.2 Kanban Board Structure

```
┌─────────────┬─────────────┬─────────────┬─────────────┬─────────────┐
│   BACKLOG   │    TODO     │ IN PROGRESS │   REVIEW    │    DONE     │
├─────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│             │             │             │             │             │
│ Unplanned   │ Ready to    │ Currently   │ Code        │ Completed   │
│ tasks       │ start       │ working on  │ review/     │ tasks       │
│             │             │             │ testing     │             │
└─────────────┴─────────────┴─────────────┴─────────────┴─────────────┘
```

### 2.3 WIP Limits
- In Progress: Max 2 per person
- Review: Max 4 total

### 2.4 Board Screenshots
*(Include actual screenshots from GitHub Projects)*

---

## 3. Work Distribution

### 3.1 Initial Distribution (Week 1-2)
| Task | Assigned To | Estimated Hours |
|------|-------------|-----------------|
| Requirements Analysis | All | 8 |
| UML Diagrams | Member 1 | 10 |
| Database Design | Member 2 | 6 |
| Architecture Design | All | 4 |

### 3.2 Development Phase (Week 3-8)
| Component | Assigned To | Hours Spent |
|-----------|-------------|-------------|
| Database Schema | Member 2 | 8 |
| Java Server Setup | Member 2 | 12 |
| REST Controllers | Member 2 | 16 |
| Services Layer | Member 2 | 12 |
| C# Microservice | Member 3 | 20 |
| gRPC Integration | Member 3 | 10 |
| JavaFX Views | Member 4 | 16 |
| ViewModels | Member 4 | 12 |
| Integration | All | 10 |

### 3.3 Final Phase (Week 9-12)
| Task | Assigned To | Hours Spent |
|------|-------------|-------------|
| Testing | All | 12 |
| Bug Fixes | All | 8 |
| Documentation | Member 1 | 10 |
| Presentation | All | 6 |

---

## 4. Meeting Logs

### Meeting 1 - Project Kickoff
**Date**: Week 1, Day 1
**Attendees**: All members
**Duration**: 2 hours

**Agenda**:
- Project requirements review
- Technology stack discussion
- Role assignment
- Initial task breakdown

**Decisions**:
- Use Kanban methodology
- Java + C# for heterogeneous architecture
- REST + gRPC for dual protocols
- PostgreSQL for database

**Action Items**:
- [ ] Set up GitHub repository
- [ ] Create Kanban board
- [ ] Install development tools

---

### Meeting 2 - Design Review
**Date**: Week 2, Day 3
**Attendees**: All members
**Duration**: 1.5 hours

**Agenda**:
- Review UML diagrams
- Database schema review
- API design discussion

**Decisions**:
- Approved domain model
- Finalized REST endpoints
- Defined proto files for gRPC

**Action Items**:
- [ ] Create database schema
- [ ] Set up Spring Boot project
- [ ] Create .NET project

---

### Meeting 3 - Sprint Review 1
**Date**: Week 4
**Attendees**: All members
**Duration**: 1 hour

**Progress**:
- Database schema completed
- Java server basic structure ready
- C# project initialized

**Issues**:
- gRPC proto compilation issues

**Action Items**:
- [ ] Fix proto compilation
- [ ] Continue REST endpoints
- [ ] Start JavaFX client

---

### Meeting 4 - Integration Planning
**Date**: Week 6
**Attendees**: All members
**Duration**: 1.5 hours

**Agenda**:
- Integration testing plan
- gRPC communication testing
- Client-server connection

**Decisions**:
- Daily integration tests
- Use Postman for REST testing
- Create test scripts

---

### Meeting 5 - Final Review
**Date**: Week 11
**Attendees**: All members
**Duration**: 2 hours

**Agenda**:
- Final demo walkthrough
- Documentation review
- Presentation preparation

**Decisions**:
- Final bug fixes assigned
- Documentation format approved
- Presentation roles assigned

---

## 5. Tools Used

### 5.1 Development Tools
| Tool | Purpose |
|------|---------|
| IntelliJ IDEA | Java development |
| Rider | C# development |
| Cursor | AI-assisted coding |
| VS Code | Documentation, scripts |
| Scene Builder | JavaFX UI design |

### 5.2 Collaboration Tools
| Tool | Purpose |
|------|---------|
| GitHub | Version control, project management |
| Discord | Team communication |
| Astah UML | UML diagrams |
| Postman | API testing |

### 5.3 Build Tools
| Tool | Purpose |
|------|---------|
| Maven | Java build |
| .NET CLI | C# build |
| PostgreSQL | Database |
| pgAdmin | Database management |

---

## 6. Challenges & Solutions

### 6.1 Challenge: gRPC Integration
**Problem**: Proto files not compiling correctly across Java and C#
**Solution**: Standardized proto file location, updated build configurations

### 6.2 Challenge: JWT Implementation
**Problem**: Token validation failing between requests
**Solution**: Proper token storage in client, correct header format

### 6.3 Challenge: MVVM Learning Curve
**Problem**: Team unfamiliar with JavaFX property bindings
**Solution**: Pair programming sessions, online tutorials

### 6.4 Challenge: Database Schema Changes
**Problem**: Late changes required migration updates
**Solution**: Used Flyway for version-controlled migrations

---

## 7. Reflection on Collaboration

### 7.1 What Worked Well
- Kanban flexibility suited our varying schedules
- GitHub Projects provided good visibility
- Regular meetings kept everyone aligned
- Code reviews improved quality

### 7.2 What Could Be Improved
- Earlier integration testing
- Better documentation during development
- More frequent code reviews
- Clearer task dependencies

### 7.3 Key Learnings
- Importance of communication in distributed teams
- Value of continuous integration
- Benefits of early prototyping
- Need for comprehensive testing

---

## 8. Time Tracking Summary

| Phase | Planned Hours | Actual Hours |
|-------|---------------|--------------|
| Analysis & Design | 30 | 28 |
| Development | 100 | 115 |
| Testing | 20 | 25 |
| Documentation | 15 | 18 |
| **Total** | **165** | **186** |

---

## 9. Conclusion

The Kanban methodology proved effective for our team, allowing flexibility while maintaining visibility. Despite challenges with integration and new technologies, the team collaborated effectively to deliver a functional distributed system.

Key success factors:
- Clear role distribution
- Regular communication
- Willingness to learn new technologies
- Collaborative problem-solving
