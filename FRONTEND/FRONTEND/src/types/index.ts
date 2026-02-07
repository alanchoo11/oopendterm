export interface Team {
  id: number;
  name: string;
  sport: string;
  coach: string;
  location: string;
  foundedYear?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Player {
  id: number;
  firstName: string;
  lastName: string;
  fullName?: string;
  age: number;
  position: string;
  rating: number;
  teamId?: number;
  jerseyNumber?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DashboardStats {
  totalTeams: number;
  totalPlayers: number;
  totalMatches: number;
  totalTournaments: number;
  averageRating: number;
}

export interface TeamStats {
  teamsBySport: Record<string, number>;
  teamsByLocation: Record<string, number>;
  averageFoundedYear: number;
  oldestTeam?: string;
}

export interface PlayerStats {
  playersByPosition: Record<string, number>;
  averageRating: number;
  averageAge: number;
  highestRatedPlayer?: string;
  highestRating?: number;
  freeAgentsCount: number;
  ratingDistribution: Record<string, number>;
}

export interface DashboardData {
  stats: DashboardStats;
  teamStats: TeamStats;
  playerStats: PlayerStats;
  recentTeams: Team[];
  topPlayers: Player[];
  freeAgents: Player[];
}

export type ViewType = 'dashboard' | 'teams' | 'players' | 'matches' | 'tournaments';

export interface NavItem {
  id: ViewType;
  label: string;
  icon: string;
  badge?: number;
}
