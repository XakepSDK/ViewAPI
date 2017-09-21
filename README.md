## ViewAPI
Simple api for creating scoreboards for players.

### Usage
```
Optional<SidebarManager> sidebarManagerOpt = Sponge.getServiceManager().provide(SidebarManager.class);
if (sidebarManagerOpt.isPresent()) {
    SidebarManager sidebarManager = sidebarManagerOpt.get();
    Sidebar sidebar = sidebarManager.createSidebar(player);
    sidebar.setTitle(new StaticText(Text.of("Title!")));
    sidebar.addLine(new StaticText(Text.of("Hello world!")));
    sidebar.addLine(sidebar.getEmptyText());
    sidebar.addLine(new StaticText(Text.of("Simple line!")));
}
```