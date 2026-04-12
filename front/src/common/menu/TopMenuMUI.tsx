import { useContext, type JSX } from 'react'
import { AppBar, Box, Button, Container, Divider, IconButton, Link, Menu, MenuItem } from "@mui/material";
import { useState } from "react";
import MenuIcon from '@mui/icons-material/Menu';
import { AccountCircle } from '@mui/icons-material';
import { UserContext } from '../../rest/models/UserContext';
import { AuthContext } from '../../App';
import { requestToken } from '../../rest/clients/AuthClient';

export interface Props<LevelProps> {
    selectBox?: JSX.Element 
    links: Array<TopMenuLink>
    sectionLinks: Array<{href: string, text: string}>
    externalLink?: JSX.Element
    levelProps?: LevelProps
}

export interface TopMenuLink {
    href: string
    content?: string
    beforeLink?: JSX.Element
    afterLink?: JSX.Element
}


const AuthPanel = (props: { userContext: UserContext | null }) => {
	const [anchorElUser, setAnchorElUser] = useState<null | HTMLElement>(null);
	const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
		setAnchorElUser(event.currentTarget);
	};
	const handleCloseUserMenu = () => {
		setAnchorElUser(null);
	};

	const loginHandler = async () => {
		localStorage.removeItem("request_token")
		const response = await requestToken()
		if (response.status === 200) {
			const url = response.data
			console.log(url)
			window.location.href = url
		}
	}

	if (props.userContext) {
		return <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0 }}>
			<Box sx={{ display: 'flex', alignItems: 'center', gap: 0 }}>
				<IconButton
					size="large"
					aria-label="account of current user"
					aria-controls="menu-appbar"
					aria-haspopup="true"
					onClick={handleOpenUserMenu}
					color="inherit"
					sx={{ display: 'flex', flexDirection: 'column', gap: 0, padding: 0, fontSize: '1rem' }}
				>
					<Box>
						<AccountCircle />
						<i className='bi bi-caret-down-fill'> </i>
					</Box>
					<Box sx={{ fontSize: '0.8rem' }}>{props.userContext.name}</Box>
				</IconButton>
				<Menu id="login-appbar"
					anchorEl={anchorElUser}
					keepMounted
					onClose={handleCloseUserMenu}
					open={Boolean(anchorElUser)}
				>
					{props.userContext.teams.map((team, index) =>
						<MenuItem href={'/team/' + team.teamId} key={'user-menu-item-' + index} >
							<Link href={'/team/' + team.teamId} color="inherit" underline="none">
								{team.teamName}
							</Link>
						</MenuItem>
					)}
					<Divider />
					<MenuItem onClick={() => {
						localStorage.removeItem("request_token")
						window.location.reload()
					}}>Log Out</MenuItem>
				</Menu>
			</Box>
		</Box>
	}
	return <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0 }}>
		<Button onClick={loginHandler} color="inherit" sx={{ padding: 1, margin: 0, minWidth: 'unset' }} href=''>Log In</Button>
	</Box>
}

export const TopMenuMUI = <Data extends object>(props: Props<Data>) => {
	const { selectBox, links } = props;
	const linksOriginalOrder = links;

	const userContext = useContext(AuthContext);

	const authPanel = <AuthPanel userContext={userContext} />

	const [anchorElNav, setAnchorElNav] = useState<null | HTMLElement>(null);

	const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
		setAnchorElNav(event.currentTarget);
	};

	const handleCloseNavMenu = () => {
		setAnchorElNav(null);
	};

	const arrow = <>&#8674;</>

	return <AppBar position="static" sx={{ backgroundColor: '#0a720a' }}>

		<Container maxWidth="xl" disableGutters>
			<Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 2, alignItems: 'center', width: '100%', justifyContent: 'space-between' }}>
				{linksOriginalOrder && linksOriginalOrder.map((link, index) => (<>
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 0 }}>

						{link.beforeLink}
						<Button key={index} href={link.href} color="inherit" 
							sx={{
								width: 'auto',
								textDecoration: 'none',
								textTransform: 'none',
								backgroundColor: 'transparent',
								fontSize: '1.4rem',
								'&:hover': {
									color: 'inherit',
									textDecoration: 'none',
									backgroundColor: 'rgba(255, 255, 255, 0.15)',
								},
							}}
>
							{link.content}
						</Button>
						{link.afterLink}
					</Box>
					{(index !== links.length - 1 || selectBox) ? arrow : <></>}

				</>
				))}
				{selectBox}
				<Box sx={{ display: 'flex', alignItems: 'center', fontSize: '1.4rem', gap: 3, whiteSpace: 'nowrap' }}>
					{props.sectionLinks.map((link, index) => {
						return <Button key={'section-link-' + index} href={link.href} color="inherit"
							sx={{
								width: 'auto',
								textDecoration: 'none',
								textTransform: 'none',
								backgroundColor: 'transparent',
								fontSize: '1.4rem',
								'&:hover': {
									color: 'inherit',
									textDecoration: 'none',
									backgroundColor: 'rgba(255, 255, 255, 0.15)',
								},
							}}
						>
							<strong>{link.text}</strong>
						</Button>
					})}
				</Box>
				<Box
					sx={{ height: 40, marginLeft: 'auto' }}
				>
					<Link href='/about'>
						<img className='logo' src="/logo.png" alt="logo" style={{ height: 40, marginLeft: 'auto' }} />
					</Link>
				</Box>
				{authPanel}
			</Box>
			<Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
				<IconButton
					size="large"
					aria-label="account of current user"
					aria-controls="menu-appbar"
					aria-haspopup="true"
					onClick={handleOpenNavMenu}
					color="inherit"
				>
					<MenuIcon />
				</IconButton>
				<Menu
					id="menu-appbar"
					anchorEl={anchorElNav}
					anchorOrigin={{
						vertical: 'bottom',
						horizontal: 'left',
					}}
					keepMounted
					transformOrigin={{
						vertical: 'top',
						horizontal: 'left',
					}}
					open={Boolean(anchorElNav)}
					onClose={handleCloseNavMenu}
					sx={{ display: { xs: 'block', md: 'none' } }}
				>

					{linksOriginalOrder && linksOriginalOrder.map((link, index) => (<MenuItem key={'menu-item-' + index}>

						<Box sx={{ display: 'flex', alignItems: 'center', gap: 0, width: '100%' }}>

							{link.beforeLink}
							<Button key={index} href={link.href} color="inherit" sx={{ padding: 1, margin: 0, width: '100%' }}>
								{link.content}
							</Button>
							{link.afterLink}
						</Box>
					</MenuItem>
					))}
					{props.sectionLinks.map((link, index) => {
						return <MenuItem key={'section_links_' + index} sx={{ display: 'flex', alignItems: 'center', gap: 0, width: '100%' }}>
							<Link href={link.href} color="inherit" underline="none" sx={{ width: '100%' }}>
								<strong>{link.text}</strong>
							</Link>
						</MenuItem>
					})}
					{selectBox}

				</Menu>
				<Box
					sx={{ height: 40, marginLeft: 'auto' }}
				>
					<Link href='/about'>
						<img className='logo' src="/logo.png" alt="logo" style={{ height: 40, marginLeft: 'auto' }} />
					</Link>
				</Box>
				{authPanel}
			</Box>
		</Container>
	</AppBar >
}
