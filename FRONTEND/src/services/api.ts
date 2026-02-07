import type { Team, Player, DashboardData, DashboardStats } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  private async fetch<T>(endpoint: string, options?: RequestInit): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    return data.data || data;
  }

  // Dashboard
  async getDashboardData(): Promise<DashboardData> {
    return this.fetch<DashboardData>('/dashboard');
  }

  async getDashboardStats(): Promise<DashboardStats> {
    return this.fetch<DashboardStats>('/dashboard/stats');
  }

  // Teams
  async getTeams(): Promise<Team[]> {
    return this.fetch<Team[]>('/teams');
  }

  async getTeam(id: number): Promise<Team> {
    return this.fetch<Team>(`/teams/${id}`);
  }

  async createTeam(team: Omit<Team, 'id'>): Promise<Team> {
    return this.fetch<Team>('/teams', {
      method: 'POST',
      body: JSON.stringify(team),
    });
  }

  async updateTeam(id: number, team: Partial<Team>): Promise<Team> {
    return this.fetch<Team>(`/teams/${id}`, {
      method: 'PUT',
      body: JSON.stringify(team),
    });
  }

  async deleteTeam(id: number): Promise<boolean> {
    return this.fetch<boolean>(`/teams/${id}`, {
      method: 'DELETE',
    });
  }

  async getTeamsBySport(sport: string): Promise<Team[]> {
    return this.fetch<Team[]>(`/teams?sport=${encodeURIComponent(sport)}`);
  }

  // Players
  async getPlayers(): Promise<Player[]> {
    return this.fetch<Player[]>('/players');
  }

  async getPlayer(id: number): Promise<Player> {
    return this.fetch<Player>(`/players/${id}`);
  }

  async createPlayer(player: Omit<Player, 'id'>): Promise<Player> {
    return this.fetch<Player>('/players', {
      method: 'POST',
      body: JSON.stringify(player),
    });
  }

  async updatePlayer(id: number, player: Partial<Player>): Promise<Player> {
    return this.fetch<Player>(`/players/${id}`, {
      method: 'PUT',
      body: JSON.stringify(player),
    });
  }

  async deletePlayer(id: number): Promise<boolean> {
    return this.fetch<boolean>(`/players/${id}`, {
      method: 'DELETE',
    });
  }

  async getPlayersByTeam(teamId: number): Promise<Player[]> {
    return this.fetch<Player[]>(`/players?teamId=${teamId}`);
  }

  async getTopPlayers(limit: number): Promise<Player[]> {
    return this.fetch<Player[]>(`/players?top=${limit}`);
  }

  async getFreeAgents(): Promise<Player[]> {
    return this.fetch<Player[]>('/players?freeAgents=true');
  }
}

export const apiService = new ApiService();

// Mock data for development (when backend is not available)
export const mockData: DashboardData = {
  stats: {
    totalTeams: 5,
    totalPlayers: 10,
    totalMatches: 0,
    totalTournaments: 0,
    averageRating: 9.05,
  },
  teamStats: {
    teamsBySport: { Football: 2, Basketball: 2, Baseball: 1 },
    teamsByLocation: { 'Manchester, UK': 1, 'Los Angeles, USA': 1, 'New York, USA': 1, 'Madrid, Spain': 1, 'San Francisco, USA': 1 },
    averageFoundedYear: 1914.8,
    oldestTeam: 'Manchester United',
  },
  playerStats: {
    playersByPosition: { Forward: 3, Midfielder: 2, Center: 1, Outfielder: 1, Guard: 3 },
    averageRating: 9.05,
    averageAge: 30.5,
    highestRatedPlayer: 'Stephen Curry',
    highestRating: 9.9,
    freeAgentsCount: 1,
    ratingDistribution: { 'Excellent (9.0+)': 6, 'Good (8.0-8.9)': 2, 'Average (7.0-7.9)': 0, 'Below Average (<7.0)': 0 },
  },
  recentTeams: [
    { id: 1, name: 'Manchester United', sport: 'Football', coach: 'Erik ten Hag', location: 'Manchester, UK', foundedYear: 1878 },
    { id: 2, name: 'Los Angeles Lakers', sport: 'Basketball', coach: 'Darvin Ham', location: 'Los Angeles, USA', foundedYear: 1947 },
    { id: 3, name: 'New York Yankees', sport: 'Baseball', coach: 'Aaron Boone', location: 'New York, USA', foundedYear: 1901 },
    { id: 4, name: 'Real Madrid', sport: 'Football', coach: 'Carlo Ancelotti', location: 'Madrid, Spain', foundedYear: 1902 },
    { id: 5, name: 'Golden State Warriors', sport: 'Basketball', coach: 'Steve Kerr', location: 'San Francisco, USA', foundedYear: 1946 },
  ],
  topPlayers: [
    { id: 8, firstName: 'Stephen', lastName: 'Curry', age: 36, position: 'Guard', rating: 9.9, teamId: 5, jerseyNumber: 30 },
    { id: 3, firstName: 'LeBron', lastName: 'James', age: 39, position: 'Forward', rating: 9.8, teamId: 2, jerseyNumber: 23 },
    { id: 6, firstName: 'Jude', lastName: 'Bellingham', age: 20, position: 'Midfielder', rating: 9.3, teamId: 4, jerseyNumber: 5 },
    { id: 4, firstName: 'Anthony', lastName: 'Davis', age: 31, position: 'Center', rating: 9.2, teamId: 2, jerseyNumber: 3 },
    { id: 7, firstName: 'Vinicius', lastName: 'Junior', age: 23, position: 'Forward', rating: 9.1, teamId: 4, jerseyNumber: 7 },
  ],
  freeAgents: [
    { id: 10, firstName: 'Kobe', lastName: 'Bryant', age: 25, position: 'Guard', rating: 9.7, jerseyNumber: 24 },
  ],
};

export const mockTeams: Team[] = mockData.recentTeams;

export const mockPlayers: Player[] = [
  { id: 1, firstName: 'Marcus', lastName: 'Rashford', age: 26, position: 'Forward', rating: 8.5, teamId: 1, jerseyNumber: 10 },
  { id: 2, firstName: 'Bruno', lastName: 'Fernandes', age: 29, position: 'Midfielder', rating: 9.0, teamId: 1, jerseyNumber: 8 },
  { id: 3, firstName: 'LeBron', lastName: 'James', age: 39, position: 'Forward', rating: 9.8, teamId: 2, jerseyNumber: 23 },
  { id: 4, firstName: 'Anthony', lastName: 'Davis', age: 31, position: 'Center', rating: 9.2, teamId: 2, jerseyNumber: 3 },
  { id: 5, firstName: 'Aaron', lastName: 'Judge', age: 32, position: 'Outfielder', rating: 9.5, teamId: 3, jerseyNumber: 99 },
  { id: 6, firstName: 'Jude', lastName: 'Bellingham', age: 20, position: 'Midfielder', rating: 9.3, teamId: 4, jerseyNumber: 5 },
  { id: 7, firstName: 'Vinicius', lastName: 'Junior', age: 23, position: 'Forward', rating: 9.1, teamId: 4, jerseyNumber: 7 },
  { id: 8, firstName: 'Stephen', lastName: 'Curry', age: 36, position: 'Guard', rating: 9.9, teamId: 5, jerseyNumber: 30 },
  { id: 9, firstName: 'Klay', lastName: 'Thompson', age: 34, position: 'Guard', rating: 8.8, teamId: 5, jerseyNumber: 11 },
  { id: 10, firstName: 'Kobe', lastName: 'Bryant', age: 25, position: 'Guard', rating: 9.7, jerseyNumber: 24 },
];
